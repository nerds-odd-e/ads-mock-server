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

  Scenario: add same size double array symbol twice
    When POST "/array-symbols":
    """
    {
      "name": "first",
      "type": "LREAL",
      "size": 5,
      "value": [1.1, 2.2, 3.3, 4.4, 5.5]
    }
    """
    When POST "/array-symbols":
    """
    {
      "name": "second",
      "type": "LREAL",
      "size": 5,
      "value": [1.0, 2.0, 3.0, 4.0, 5.0]
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads read LREAL array symbol by name "second" and size 5 should:
    """
    = [1.0, 2.0, 3.0, 4.0, 5.0]
    """
    Then ads read LREAL array symbol by name "first" and size 5 should:
    """
    = [1.1, 2.2, 3.3, 4.4, 5.5]
    """

  Scenario: set device info
    When PUT "/device-info":
    """
    {
      "name": "szb_plc",
      "version": 1,
      "revision": 2,
      "build": 3
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads get device info should be:
    """
    = {
      name: szb_plc
      version: 1y
      revision: 2y
      build: 3s
    }
    """

  Scenario: clear all symbols
    When POST "Symbol" "/symbols":
    """
    {
      "name": "PC_PLC.b_error"
    }
    """
    When PUT "/device-info":
    """
    {
      "name": "szb_plc",
      "version": 1,
      "revision": 2,
      "build": 3
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
    Then ads get device info should be:
    """
    = {
      name: not_set
      version: 0y
      revision: 0y
      build: 0s
    }
    """
