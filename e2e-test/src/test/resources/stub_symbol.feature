Feature: Stub Symbol

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

  Scenario Outline: add <type> array symbol
    When POST "/array-symbols":
    """
    {
      "name": "PC_PLC.s_MoveVel",
      "type": "<type>",
      "size": 3,
      "value": <value>
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads read <type> array symbol by name "PC_PLC.s_MoveVel" and size 3 should:
    """
    = <expectedValue>
    """
    Examples:
      | type  | value           | expectedValue      |
      | LREAL | [1.0, 2.0, 3.0] | [1.0, 2.0, 3.0]    |
      | REAL  | [1.0, 2.0, 3.0] | [1.0f, 2.0f, 3.0f] |

  Scenario: add same name and type symbol twice with different values
    When POST "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "BOOL",
      "value": true
    }
    """
    When POST "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "BOOL",
      "value": false
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads operation should:
    """
    readBoolSymbolByName['PC_PLC.b_error'] = false
    """

#  Scenario: add same name and type array symbol twice with different size and values
#    When POST "/array-symbols":
#    """
#    {
#      "name": "PC_PLC.lreal_array",
#      "type": "LREAL",
#      "size": 4,
#      "value": [1.1, 2.2, 3.3, 4.4]
#    }
#    """
#    When POST "/array-symbols":
#    """
#    {
#      "name": "PC_PLC.lreal_array",
#      "type": "LREAL",
#      "size": 5,
#      "value": [1.0, 2.0, 3.0, 4.0, 5.0]
#    }
#    """
#    Then response should be:
#    """
#    code= 200
#    """
#    Then ads read LREAL array symbol by name "PC_PLC.lreal_array" and size 5 should:
#    """
#    = [1.0, 2.0, 3.0, 4.0, 5.0]
#    """

  Scenario: add same type and size array symbol twice with different names
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
