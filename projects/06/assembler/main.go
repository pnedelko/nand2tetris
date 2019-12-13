package main

import (
	"bufio"
	"fmt"
	"github.com/pnedelko/nand2tetris/assembler/code"
	"github.com/pnedelko/nand2tetris/assembler/parser"
	"github.com/pnedelko/nand2tetris/assembler/symboltable"
	"log"
	"os"
	"path/filepath"
	"strconv"
	// "strings"
)

func check(e error) {
	if e != nil {
		panic(e)
	}
}

func main() {
	if len(os.Args) < 2 {
		panic("You should provide a filepath as an argument")
	}

	asmFile := os.Args[1]

	absolutePath, _ := filepath.Abs(asmFile)
	fmt.Println("Absolute path:", absolutePath)

	dir := filepath.Dir(absolutePath)
	fmt.Println("Dir:", dir)

	base := filepath.Base(absolutePath)
	fmt.Println("Base:", base)

	ext := filepath.Ext(base)
	fmt.Println("Ext:", ext)

	if ext != ".asm" {
		log.Fatal("Only .asm files accepted")
	}

	//@todo: close file!!!
	outputFilename := base[0:len(base)-4] + ".hack"
	fmt.Println("Output file:", outputFilename)

	st := symboltable.NewSymbolTable()
	predefinedSymbols := map[string]int{
		"SP":     0,
		"LCL":    1,
		"ARG":    2,
		"THIS":   3,
		"THAT":   4,
		"R0":     0,
		"R1":     1,
		"R2":     2,
		"R3":     3,
		"R4":     4,
		"R5":     5,
		"R6":     6,
		"R7":     7,
		"R8":     8,
		"R9":     9,
		"R10":    10,
		"R11":    11,
		"R12":    12,
		"R13":    13,
		"R14":    14,
		"R15":    15,
		"SCREEN": 16384,
		"KBD":    24576,
	}

	for key, value := range predefinedSymbols {
		st.AddEntry(key, value)
	}

	fo, err := os.Open(absolutePath)
	check(err)
	defer fo.Close()

	p1 := parser.NewParser(fo)
	var pc int = 0 //program counter

	for p1.HasMoreCommands() {
		p1.Advance()

		if p1.CommandType() == parser.ACommand {
			// fmt.Println("Found A command", p1.CurrentCommand(), pc)
			pc++
		} else if p1.CommandType() == parser.CCommand {
			// fmt.Println("Found C command", p1.CurrentCommand(), pc)
			pc++
		} else if p1.CommandType() == parser.LCommand {
			// fmt.Println("Found L command", p1.CurrentCommand(), pc)
			st.AddEntry(p1.Symbol(), pc)
		} else {
			continue
		}
	}

	// open output file
	fw, err := os.Create(filepath.Join(dir, outputFilename))
	check(err)
	defer fw.Close()
	w := bufio.NewWriter(fw)

	// second pass
	fo2, err := os.Open(absolutePath)
	check(err)
	defer fo2.Close()
	p2 := parser.NewParser(fo2)
	varsPointer := 16

	for p2.HasMoreCommands() {
		p2.Advance()

		if p2.CommandType() == parser.ACommand {
			symbol := p2.Symbol()
			num, err := strconv.Atoi(symbol)

			//symbol is not numeric
			if err != nil {
				if st.Contains(symbol) {
					num = st.GetAddress(symbol)
				} else {
					// new var
					st.AddEntry(symbol, varsPointer)
					num = varsPointer
					varsPointer++
				}
			}

			_, werr := w.WriteString(fmt.Sprintf("%016b\n", num))
			check(werr)
		} else if p2.CommandType() == parser.CCommand {
			comp := code.Comp(p2.Comp())
			dest := code.Dest(p2.Dest())
			jump := code.Jump(p2.Jump())
			line := fmt.Sprintf("111" + fmt.Sprintf("%07b", comp) + fmt.Sprintf("%03b", dest) + fmt.Sprintf("%03b", jump) + "\n")
			_, err := w.WriteString(line)
			check(err)
		} else {
			continue
		}
	}

	w.Flush()
}
