// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

//initial offset = -1
@offset
M=-1

//max offset = 8192-1 (don't ask why)
@8191
D=A
@moffset
M=D

(LOOP)
    @KBD
    D=M

    //if a key is pressed - write black pixels
    @BLACK
    D;JGT

    //else write white pixels
    @WHITE
    0;JMP

(BLACK)
    // if offset == maxOffset -> return
    @offset
    D=M
    @moffset
    D=M-D
    @LOOP
    D;JEQ

    //increment offset
    @offset
    M=M+1

    //make all pixels at the current offset black
    @offset
    D=M
    @SCREEN
    A=D+A // D + offset
    M=M-1 //set pixel to black

    @LOOP
    0;JMP

(WHITE)
    //if offset is < 0, then we are done
    @offset
    D=M
    @LOOP
    D;JLT

    //make all pixels at the current offset white
    @offset
    D=M
    @SCREEN
    A=D+A // D + offset
    M=0 //set pixel to white

    //decrement offset
    @offset
    M=M-1

    @LOOP
    0;JMP
