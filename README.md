# Bruhdroid - May 2022
This project is a block programming language for android devices
<br>
Bruhdroid - приложение для Android, позволяющее писать код с помощью блоков <br>
Проект был написан в рамках академического курса "Мобильная разработка" и дальнейшая разработка не планируется

<br> <br>

# Project developers:
<ul>
  <li>N: Batrakov Oleg
  <li>G: Cortuzz
</ul>
<ul>
  <li>N: Chaunin Vyacheslav
  <li>G: Ssslakter
</ul>
<ul>
  <li>N: Tarasova Alyona
  <li>G: alyoneek
</ul>

# Features
### Write block code
![image](https://github.com/user-attachments/assets/bc365b7d-e8f1-42ea-9a84-f2ebf9cfad8c)

### Run it
![image](https://github.com/user-attachments/assets/40f774ef-4c76-4ef8-b3de-83be762d129a)
![image](https://github.com/user-attachments/assets/5376c8ed-a723-4eea-b85d-ffdf8be7bec2)

### Templates
![image](https://github.com/user-attachments/assets/43544a41-c015-4a10-a385-7b5c63295d59)


### Debugger
![image](https://github.com/user-attachments/assets/39415d07-854e-4690-9bd0-0e870596f482)

### Error Handling
![image](https://github.com/user-attachments/assets/11284416-aaf2-45ed-9218-5cc232c6a5b7)

### Dark theme
![image](https://github.com/user-attachments/assets/5ba8cddb-5f58-4e32-94cb-6a25cc637f75)
![image](https://github.com/user-attachments/assets/29502a8d-df0e-41e0-8bea-f28a62842545)

# Blocks:
### Variables
![image](https://user-images.githubusercontent.com/52497929/169803692-5a269cb8-cbc5-4dc7-b4be-cb4f72c23f36.png) <br>
_Blocks from this group declare or assign a specific value to a variable_ <br> <br>
The difference is that the INIT block creates a variable in the local scope and assigns a value to it, while the SET block does not, 
but only changes the value of an existing variable.

### Standart IO
![image](https://user-images.githubusercontent.com/52497929/169805392-6d69c381-7c06-4681-b39e-a35e3ad45ac4.png) <br>
_Blocks from this group work with I/O_ <br> <br>
The PRAGMA block specifies system presets (for example, INIT_MESSAGE = false will turn off the welcome message in the console); <br>
The INPUT block asks for user input; <br>
The PRINT block outputs the result of processing the expression to the console.

### Loops
![image](https://user-images.githubusercontent.com/52497929/169805770-e6d87ac8-c114-4e28-90a2-8dea274213bc.png) <br>
_Blocks of this group allow you to work with loops_ <br> <br>
The WHILE block will be executed until the condition becomes false; <br>
The FOR block contains 3 parts: variable initialization, a condition, and an action that will happen at the end of each iteration; <br>
The CONTINUE block immediately stops the current iteration and proceeds to the next one; <br>
The break block interrupts the execution of the loop. 

### Conditions
![image](https://user-images.githubusercontent.com/52497929/169806592-556b0509-d1ab-4bb6-b80d-1949cf68c05e.png) <br>
_This category contains one expandable condition block_ <br> <br>
The contents of the IF block will be executed if the result of the expression of this block is true; <br>
The contents of the ELIF block will be executed if the result of the expression of this block is true, and all previous blocks of conditions were false; <br>
The contents of the ELSE block will be executed if the results of the expressions of all previous condition blocks turned out to be false; <br>

### Functions
Not documented yet



