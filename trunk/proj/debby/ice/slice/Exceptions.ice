// This file defines exceptions could be thrown by session service.

module debby
{
    module slice
    {
        exception SessionException
        {
            string reason;
        };

        exception FileNotExist extends SessionException
        {
        };

        exception UnknownSession extends SessionException
        {
        };

        exception DuplicateKeepAlive extends SessionException
        {
        };
    };
};

