package org.etma.main.pojos;

public class Error
{
    private Object validationErrors;

    private String message;

    private Object details;

    private String code;

    public Object getValidationErrors ()
    {
        return validationErrors;
    }

    public void setValidationErrors (Object validationErrors)
    {
        this.validationErrors = validationErrors;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public Object getDetails ()
    {
        return details;
    }

    public void setDetails (Object details)
    {
        this.details = details;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }


}
