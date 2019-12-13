package main

import (
	"bufio"
	"fmt"
	"github.com/pnedelko/nand2tetris/assembler/code"
	"github.com/pnedelko/nand2tetris/assembler/parser"
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

	fw, err := os.Create(filepath.Join(dir, outputFilename))
	check(err)
	defer fw.Close()
	w := bufio.NewWriter(fw)

	fo, err := os.Open(absolutePath)
	check(err)
	defer fo.Close()

	p := parser.NewParser(fo)
	for p.HasMoreCommands() {
		p.Advance()

		if p.CommandType() == parser.ACommand {
			num, _ := strconv.Atoi(p.Symbol())
			_, err := w.WriteString(fmt.Sprintf("%016b\n", num))
			check(err)
		} else if p.CommandType() == parser.CCommand {
			comp := code.Comp(p.Comp())
			dest := code.Dest(p.Dest())
			line := fmt.Sprintf("111" + fmt.Sprintf("%07b", comp) + fmt.Sprintf("%03b", dest) + "000\n")
			_, err := w.WriteString(line)
			check(err)
		} else {
			continue
		}
	}

	w.Flush()
}

// func parseCommandA(line string) string {
// 	command := line[1:]

// 	if isInt(command) {
// 		num, _ := strconv.Atoi(command)
// 		return formatInt(num)
// 	}
// 	// variable
// 	return "Variable..."
// }

// func isInt(str string) bool {
// 	if _, err := strconv.Atoi(str); err == nil {
// 		return true
// 	}
// 	return false
// }

// func formatInt(num int) string {
// 	return fmt.Sprintf("%016b", num)
// }
