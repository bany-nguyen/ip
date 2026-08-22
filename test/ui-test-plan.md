# UI Test Plan

## How to use this plan

Each test case starts a fresh Bany process. The expected-output block is the complete output of that run, including the startup banner, divider lines, blank lines, and spaces. Add new test cases in the same format.

The program is compiled with Java 25 before the cases run:

```bash
javac -cp 'commons-lang3-3.20.0/commons-lang3-3.20.0.jar:jackson-2.21/*' -d _temp/test-ui-classes $(rg --files src/main/java -g '*.java')
```

## Test case: Exit cleanly

**Aim:** Verify that Bany accepts `bye` and shows its farewell message.

**Inputs:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Add and list a to-do

**Aim:** Verify that a to-do can be added and then appears in the task list.

**Inputs:**

```text
todo read book
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Got it. I've added this task:
  [T][] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Allow an extra non-critical tag

**Aim:** Verify that an extra tag produces a warning but does not prevent task creation.

**Inputs:**

```text
todo run /open haha
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Warning: the command contains extra tags or tags in an unexpected order.
The task will still be added.
____________________________________________________________
Got it. I've added this task:
  [T][] run
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Allow required tags in a different order

**Aim:** Verify that an event with `/to` before `/from` is created, with a warning.

**Inputs:**

```text
event run so much i die /to 4 /from 2
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Warning: the command contains extra tags or tags in an unexpected order.
The task will still be added.
____________________________________________________________
Got it. I've added this task:
  [E][] run so much i die (from: 2 to: 4)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Allow duplicate non-critical tags

**Aim:** Verify that duplicate non-critical tags produce a warning but do not prevent event creation.

**Inputs:**

```text
event run so much i die /frOm 4 /die 2 /die 3 /to 2
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Warning: the command contains extra tags or tags in an unexpected order.
The task will still be added.
____________________________________________________________
Got it. I've added this task:
  [E][] run so much i die (from: 4 to: 2)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Accept a deadline tag in mixed case

**Aim:** Verify that tag names are case-insensitive for valid required tags.

**Inputs:**

```text
deadline bleh /bY 12
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Got it. I've added this task:
  [D][] bleh (by: 12)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Reject duplicate critical deadline tags

**Aim:** Verify that a deadline with two `/by` tags is rejected.

**Inputs:**

```text
deadline bleh /by 12 /BY 6
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
The tag /by can only be used once!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Reject a tag used as the command

**Aim:** Verify that the first token must be a valid command.

**Inputs:**

```text
/by 12 deadline bleh
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Command not found. Do you mean BYE?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Suggest the closest command

**Aim:** Verify that Bany suggests the allowed command with the smallest Levenshtein distance.

**Inputs:**

```text
lits
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Command not found. Do you mean LIST?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```


## Test case: Load persisted tasks at startup

**Aim:** Verify that Bany loads a saved task from `data/bany.txt` before accepting commands.

**Setup:** Before launching Bany, write this JSON to `data/bany.txt`:

```json
[{"id":7,"type":"DEADLINE","description":"submit report","done":true,"by":"Friday"}]
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____
| __ )  __ _ _ __  _   _
|  _ \ / _` | '_ \| | | |
| |_) | (_| | | | | |_| |
|____/ \__,_|_| |_|\__, |
                   |___/
Hello! I'm Bany.
What can I do for you?
____________________________________________________________

____________________________________________________________
Here are the tasks in your list:
1.[D][X] submit report (by: Friday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
