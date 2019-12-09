package main

import (
	"os"
	"bufio"
	"log"
	"fmt"
	"strings"
	"strconv"
)

var compMap = map[string]int{
	"0": 0b0101010,
	"1": 0b0111111,
	"-1": 0b0111010,
	"D": 0b0001100,
	"A": 0b0110000,
	"!D": 0b0001101,
	"!A": 0b0110001,
	"-D": 0b0001111,
	"-A": 0b0110011,
	"D+1": 0b0011111,
	"A+1": 0b0110111,
	"D-1": 0b0001110,
	"A-1": 0b0110010,
	"D+A": 0b0000010,
	"D-A": 0b0010011,
	"A-D": 0b0000111,
	"D&A": 0b0000000,
	"D|A": 0b0010101,
	// //1
	"M": 0b1110000,
	"!M": 0b1110001,
	"-M": 0b1110011,
	"M+1": 0b1110111,
	"M-1": 0b1110010,
	"D+M": 0b1000010,
	"D-M": 0b1010011,
	"M-D": 0b1000111,
	"D&M": 0b1000000,
	"D|M": 0b1010101,
};

var destMap = map[string]int{
	"null": 0b000,
	"M": 0b001,
	"D": 0b010,
	"MD": 0b011,
	"A": 0b100,
	"AM": 0b101,
	"AD": 0b110,
	"AMD": 0b111,
}

var jumpMap = map[string]int{
	"null": 0b000,
	"JGT": 0b001,
	"JEQ": 0b010,
	"JGE": 0b011,
	"JLT": 0b100,
	"JNE": 0b101,
	"JLE": 0b110,
	"JMP": 0b111,
}

func main()  {
	if len(os.Args) < 2 {
		panic("You should provide a filepath as an argument")
	}

	filePath := os.Args[1]
	file, err := os.Open(filePath)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())
		newLine := processLine(line)
		if newLine != "" {
			fmt.Println(newLine)
		}
	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}
}


func processLine(line string) string {
	if line == "" {
		return ""
	}

	if strings.Index(line, "//") >= 0 {
		return ""
	}

	if strings.Index(line, "@") == 0 {
		res := parseCommandA(line)
		return res
	}

	return parseCommandC(line)
}

func parseCommandA(line string) string {
	command := line[1:]
	
	if isInt(command) {
		num, _ := strconv.Atoi(command)
		return formatInt(num)
	} else {
		// variable
		return "Variable..."
	}

	return ""
}

func parseCommandC(line string) string {
	if strings.Index(line, "=") >= 0 {
		res := strings.Split(line, "=")
		dest := res[0]
		comp := res[1]

		destCode := destMap[dest]
		compCode := compMap[comp]

		return fmt.Sprintf("111" + fmt.Sprintf("%07b", compCode) + fmt.Sprintf("%03b", destCode) + "000")
	}

	return ""
}


func isInt(str string) bool  {
	if _, err := strconv.Atoi(str); err == nil {
		return true
	}
	return false
}

func formatInt(num int) string {
	return fmt.Sprintf("%016b", num)
}
