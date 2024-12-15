Feature: Symbol

  Scenario: add a symbol
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