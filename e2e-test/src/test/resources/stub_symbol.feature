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

  Scenario Outline: add same name and type <type> symbol twice with different values
    When POST "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "<type>",
      "value": <originalValue>
    }
    """
    When POST "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "<type>",
      "value": <newValue>
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads operation should:
    """
    <adsOpt>['PC_PLC.b_error']: <newValue>
    """
    Examples:
      | type  | originalValue | newValue | adsOpt                |
      | BOOL  | true          | false    | readBoolSymbolByName  |
      | INT   | 42            | 123      | readIntSymbolByName   |
      | DINT  | 42            | 123      | readDIntSymbolByName  |
      | REAL  | 123.06        | 42       | readRealSymbolByName  |
      | LREAL | 123.06        | 42       | readLRealSymbolByName |

  Scenario: add same name symbol twice with different type will fail
    When POST "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "BOOL",
      "value": true
    }
    """
    When POST "Symbol" "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "INT"
    }
    """
    Then response should be:
    """
    : {
      code= 400
      body.json= {
        "error": "The type of the existing symbol does not match the requested type."
      }
    }
    """
    Then ads operation should:
    """
    readBoolSymbolByName['PC_PLC.b_error'] = true
    """

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
      | type  | value               | expectedValue       |
      | BOOL  | [true, false, true] | [true, false, true] |
      | LREAL | [1.0, 2.0, 3.0]     | [1.0, 2.0, 3.0]     |
      | REAL  | [1.0, 2.0, 3.0]     | [1.0f, 2.0f, 3.0f]  |

  Scenario Outline: add same name and type <type> and size array symbol twice with different values
    When POST "/array-symbols":
    """
    {
      "name": "PC_PLC.lreal_array",
      "type": "<type>",
      "size": 4,
      "value": <originalValue>
    }
    """
    When POST "/array-symbols":
    """
    {
      "name": "PC_PLC.lreal_array",
      "type": "<type>",
      "size": 4,
      "value": <newValue>
    }
    """
    Then response should be:
    """
    code= 200
    """
    Then ads read <type> array symbol by name "PC_PLC.lreal_array" and size 4 should:
    """
    : <newValue>
    """
    Examples:
      | type  | originalValue        | newValue             |
      | BOOL  | [true, false, false, true]   | [false, true, false, true] |
      | REAL  | [1.0, 2.0, 3.0, 4.0] | [5.0, 6.0, 7.0, 8.0] |
      | LREAL | [1.0, 2.0, 3.0, 4.0] | [5.0, 6.0, 7.0, 8.0] |

  Scenario: add same name array symbol twice with different type will fail
    When POST "/array-symbols":
    """
    {
      "name": "PC_PLC.array",
      "type": "LREAL",
      "size": 4,
      "value": [1.0, 2.0, 3.0, 4.0]
    }
    """
    When POST "/array-symbols":
    """
    {
      "name": "PC_PLC.array",
      "type": "REAL",
      "size": 4,
      "value": [1.0, 2.0, 3.0, 4.0]
    }
    """
    Then response should be:
    """
    : {
      code= 400
      body.json= {
        "error": "The type of the existing symbol does not match the requested type."
      }
    }
    """
    Then ads read LREAL array symbol by name "PC_PLC.array" and size 4 should:
    """
    : [1.0, 2.0, 3.0, 4.0]
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
