# UI Test Plan

## How to use this plan

Each test case starts a fresh Bany process. The expected-output block is the complete output of that run, including the startup banner, divider lines, blank lines, and spaces. Add new test cases in the same format.

The program is compiled with Java 25 before the cases run:

```bash
javac -d _temp/test-ui-classes src/main/java/*.java
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
[T] [ ] read book 
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T] [ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
