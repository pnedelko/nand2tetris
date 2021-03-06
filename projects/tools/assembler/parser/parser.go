package parser

import (
	"bufio"
	// "fmt"
	"os"
	"strings"
)

const ACommand string = "A"
const CCommand string = "C"
const LCommand string = "L"

type Parser struct {
	scanner        *bufio.Scanner
	currentCommand string
}

func NewParser(file *os.File) *Parser {
	scanner := bufio.NewScanner(file)
	return &Parser{scanner, ""}
}

func (p *Parser) CurrentCommand() string {
	return p.currentCommand
}

func (p *Parser) HasMoreCommands() bool {
	// if err := scanner.Err(); err != nil {
	// 	log.Fatal(err)
	// }

	return p.scanner.Scan()
}

func (p *Parser) Advance() {
	line := p.scanner.Text()
	commentIndex := strings.Index(line, "//")
	if commentIndex >= 0 {
		line = line[0:commentIndex]
	}
	p.currentCommand = strings.TrimSpace(line)
}

func (p *Parser) CommandType() string {
	cmd := p.currentCommand

	if cmd == "" {
		return ""
	}

	if strings.Index(cmd, "@") == 0 {
		return ACommand
	}

	if strings.Index(cmd, "(") == 0 && strings.Index(cmd, ")") == len(cmd)-1 {
		return LCommand
	}

	return CCommand
}

func (p *Parser) Symbol() string {
	if p.CommandType() == ACommand {
		return p.currentCommand[1:]
	} else if p.CommandType() == LCommand {
		return p.currentCommand[1 : len(p.currentCommand)-1]
	}
	panic("Symbol is only allowed for A and L commands, but not for " + p.currentCommand)
}

func (p *Parser) Dest() string {
	if strings.Index(p.currentCommand, "=") > 0 {
		res := strings.Split(p.currentCommand, "=")
		return res[0]
	} else if strings.Index(p.currentCommand, ";") > 0 {
		return "null"
	}
	return ""
}

func (p *Parser) Comp() string {
	if strings.Index(p.currentCommand, "=") >= 0 {
		res := strings.Split(p.currentCommand, "=")
		return res[1]
	} else if strings.Index(p.currentCommand, ";") >= 0 {
		res := strings.Split(p.currentCommand, ";")
		return res[0]
	}
	return ""
}

func (p *Parser) Jump() string {
	if strings.Index(p.currentCommand, "=") >= 0 {
		return "null"
	} else if strings.Index(p.currentCommand, ";") >= 0 {
		res := strings.Split(p.currentCommand, ";")
		return res[1]
	}
	return ""
}
