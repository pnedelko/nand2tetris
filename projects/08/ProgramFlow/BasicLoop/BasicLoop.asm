//PUSH constant 0
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
//POP local 0
@SP
M=M-1
A=M
D=M
@R13
M=D
@0
D=A
@LCL
A=M+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
(LOOP_START)
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
//PUSH local 0
@0
D=A
@LCL
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
//POP local 0
@SP
M=M-1
A=M
D=M
@R13
M=D
@0
D=A
@LCL
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
//if-goto LOOP_START
@SP
M=M-1
A=M
D=M
@LOOP_START
D;JNE
//PUSH local 0
@0
D=A
@LCL
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
