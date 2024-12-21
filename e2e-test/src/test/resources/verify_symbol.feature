Feature: Verify Symbol

  Scenario: verify written symbol
    Given POST "Symbol" "/symbols":
    """
    {
      "name": "first",
      "type": "INT"
    }
    """
    When write "INT" symbol "first" with new value 12306
    Then "/symbols" should response:
    """
    : {
      code= 200
      body.json= {
       first: 12306s
      }
    }
    """