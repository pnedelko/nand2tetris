@256
D=A
@SP
M=D
//PUSH constant 10
@10
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
//PUSH constant 21
@21
D=A
@SP
A=M
M=D
@SP
M=M+1
//PUSH constant 22
@22
D=A
@SP
A=M
M=D
@SP
M=M+1
//POP argument 2
@SP
M=M-1
A=M
D=M
@R13
M=D
@2
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
//POP argument 1
@SP
M=M-1
A=M
D=M
@R13
M=D
@1
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
//PUSH constant 36
@36
D=A
@SP
A=M
M=D
@SP
M=M+1
//POP this 6
@SP
M=M-1
A=M
D=M
@R13
M=D
@6
D=A
@THIS
A=M+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
//PUSH constant 42
@42
D=A
@SP
A=M
M=D
@SP
M=M+1
//PUSH constant 45
@45
D=A
@SP
A=M
M=D
@SP
M=M+1
//POP that 5
@SP
M=M-1
A=M
D=M
@R13
M=D
@5
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
//PUSH constant 510
@510
D=A
@SP
A=M
M=D
@SP
M=M+1
//POP temp 6
@SP
M=M-1
A=M
D=M
@R13
M=D
@6
D=A
@5
A=A+D
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
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
//PUSH that 5
@5
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
//PUSH this 6
@6
D=A
@THIS
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
//PUSH this 6
@6
D=A
@THIS
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
//PUSH temp 6
@6
D=A
@5
A=A+D
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
