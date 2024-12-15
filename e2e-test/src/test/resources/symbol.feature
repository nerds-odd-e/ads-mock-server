Feature: Symbol

  Scenario: add a symbol
    When POST "/symbols":
    """
    {
      "name": "PC_PLC.b_error",
      "type": "INT",
      "value": 0
    }
    """
    Then ads operation should:
    """

    """