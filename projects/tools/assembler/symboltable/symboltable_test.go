package symboltable

import (
	"reflect"
	"testing"
)

func TestSymbolTable_AllMethods(t *testing.T) {
	s := NewSymbolTable()
	symbol := "SYMBOL"
	addr := 1000
	s.AddEntry(symbol, addr)

	t.Run("should get right address", func(t *testing.T) {
		if got := s.GetAddress(symbol); !reflect.DeepEqual(got, addr) {
			t.Errorf("GetAddress() = %v, want %v", got, addr)
		}
	})

	t.Run("should return proper contains", func(t *testing.T) {
		if got := s.Contains(symbol); !reflect.DeepEqual(got, true) {
			t.Errorf("Contains() = %v, want %v", got, true)
		}
	})

	t.Run("should return false for non existing symbol", func(t *testing.T) {
		if got := s.Contains("doesnt_exist"); !reflect.DeepEqual(got, false) {
			t.Errorf("Contains() = %v, want %v", got, false)
		}
	})
}
