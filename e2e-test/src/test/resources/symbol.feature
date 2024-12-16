Feature: Symbol

  Scenario: add an int symbol
    When POST "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "INT",
      "value": 42
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads operation should:
    """
    readIntSymbolByName['PC_PLC.b_error']= 42s
    """

  Scenario: clear all symbols
    When POST "Symbol" "/symbols":
    """
    {
      "name": "PC_PLC.b_error"
    }
    """
    When DELETE "/symbols"
    Then response should be:
    """
    code= 200
    """
    Then ads operation should:
    """
    readIntSymbolByName['PC_PLC.b_error']::throw.message= 'Getting handler by name: PC_PLC.b_error failed with error code: 1808'
    """
