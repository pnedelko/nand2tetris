@256
D=A
@SP
M=D
//call Sys.init 0
@RETURN_Sys.init
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@0
D=D-A
@5
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@Sys.init
0;JMP
(RETURN_Sys.init)
//PUSH argument 1
@1
D=A
@ARG
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
//POP pointer 1
@SP
M=M-1
A=M
D=M
@R13
M=D
@1
D=A
@3
A=A+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
//PUSH constant 0
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
//POP that 0
@SP
M=M-1
A=M
D=M
@R13
M=D
@0
D=A
@THAT
A=M+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
//PUSH constant 1
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
//POP that 1
@SP
M=M-1
A=M
D=M
@R13
M=D
@1
D=A
@THAT
A=M+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
//PUSH argument 0
@0
D=A
@ARG
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
//PUSH constant 2
@2
D=A
@SP
A=M
M=D
@SP
M=M+1
//sub
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=M-D
@SP
M=M+1
//POP argument 0
@SP
M=M-1
A=M
D=M
@R13
M=D
@0
D=A
@ARG
A=M+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
//label MAIN_LOOP_START
(MAIN_LOOP_START)
//PUSH argument 0
@0
D=A
@ARG
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
//if-goto COMPUTE_ELEMENT
@SP
M=M-1
A=M
D=M
@COMPUTE_ELEMENT
D;JNE
//goto END_PROGRAM
@END_PROGRAM
0;JMP
//label COMPUTE_ELEMENT
(COMPUTE_ELEMENT)
//PUSH that 0
@0
D=A
@THAT
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
//PUSH that 1
@1
D=A
@THAT
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
//add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=D+M
@SP
M=M+1
//POP that 2
@SP
M=M-1
A=M
D=M
@R13
M=D
@2
D=A
@THAT
A=M+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
//PUSH pointer 1
@1
D=A
@3
A=A+D
D=M
@SP
A=M
M=D
@SP
M=M+1
//PUSH constant 1
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
//add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=D+M
@SP
M=M+1
//POP pointer 1
@SP
M=M-1
A=M
D=M
@R13
M=D
@1
D=A
@3
A=A+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
//PUSH argument 0
@0
D=A
@ARG
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
//PUSH constant 1
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
//sub
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=M-D
@SP
M=M+1
//POP argument 0
@SP
M=M-1
A=M
D=M
@R13
M=D
@0
D=A
@ARG
A=M+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
//goto MAIN_LOOP_START
@MAIN_LOOP_START
0;JMP
//label END_PROGRAM
(END_PROGRAM)
