package com.henallux.exception;

public class DataAccessException extends Exception
{
    private int messageId;

    public DataAccessException()
    {
        super();
    }

    public DataAccessException(int messageId)
    {
        this.messageId = messageId;
    }

    public int getMessageId()
    {
        return messageId;
    }
}
