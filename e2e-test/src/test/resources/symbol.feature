Feature: Symbol

  Scenario Outline: add <type> symbol
    When POST "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "<type>",
      "value": <value>
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads operation should:
    """
    <adsOpt>['PC_PLC.b_error']= <expected>
    """
    Examples:
      | type  | value  | adsOpt                | expected |
      | INT   | 42     | readIntSymbolByName   | 42s      |
      | BOOL  | true   | readBoolSymbolByName  | true     |
      | BOOL  | false  | readBoolSymbolByName  | false    |
      | LREAL | 123.06 | readLRealSymbolByName | 123.06   |
      | DINT  | 42     | readDIntSymbolByName  | 42       |
      | REAL  | 123.06 | readRealSymbolByName  | 123.06f  |

  Scenario: add double array symbol
    When POST "/array-symbols":
    """
    {
      "name": "PC_PLC.s_MoveVel",
      "type": "LREAL",
      "size": 3,
      "value": [1.0, 2.0, 3.0]
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads read LREAL array symbol by name "PC_PLC.s_MoveVel" and size 3 should:
    """
    = [1.0, 2.0, 3.0]
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
