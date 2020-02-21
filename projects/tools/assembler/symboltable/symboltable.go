package symboltable

type SymbolTable struct {
	table map[string]int
}

func NewSymbolTable() *SymbolTable {
	m := make(map[string]int)
	return &SymbolTable{m}
}

func (s *SymbolTable) AddEntry(symbol string, address int) {
	s.table[symbol] = address
}

func (s *SymbolTable) Contains(symbol string) bool {
	_, ok := s.table[symbol]
	return ok
}

func (s *SymbolTable) GetAddress(symbol string) int {
	return s.table[symbol]
}
