Feature: Symbol

  Scenario: add a symbol
    When POST "/symbols":
    """
    {
      "name": "b_error",
      "type": "INT",
      "value": 0
    }
    """