Feature: Verify Symbol

  Scenario Outline: verify written <type> symbol
    Given POST "/symbols":
    """
    {
      "name": "first",
      "type": "<type>",
      "value": <old_value>
    }
    """
    When write <type> symbol "first" with new value "<new_value>"
    Then "/symbols" should response:
    """
    : {
      code= 200
      body.json= {
       first: <new_value>
      }
    }
    """
    Examples:
      | type  | old_value | new_value |
      | INT   | 57        | 12306     |
      | BOOL  | true      | false     |
      | LREAL | 1.0       | 2.0       |
      | REAL  | 3.0       | 4.0       |
      | DINT  | 23        | 42        |

  Scenario: verify written LREAL array symbol
    Given POST "/array-symbols":
    """
    {
      "name": "first",
      "type": "LREAL",
      "size": 3,
      "value": [1.0, 2.0, 3.0]
    }
    """
    When write LREAL array symbol "first" with new value:
      | 11.0 | 22.0 | 33.0 |
    Then "/symbols" should response:
    """
    : {
      code= 200
      body.json= {
       first: [11.0 22.0 33.0]
      }
    }
    """
