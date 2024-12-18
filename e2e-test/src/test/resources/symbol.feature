Feature: Symbol

  Scenario Outline: add an <type> symbol
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
      | type | value | adsOpt               | expected |
      | INT  | 42    | readIntSymbolByName  | 42s      |
      | BOOL | true  | readBoolSymbolByName | true     |
      | BOOL | false | readBoolSymbolByName | false    |

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
