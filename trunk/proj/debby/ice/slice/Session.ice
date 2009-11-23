#include <Exceptions.ice>

module debby 
{
    module slice
    {

        enum EventType {ECreated, ERemoved, EChanged, EDirChanged, EElected, ENothing};

        struct Event {
            EventType type;
            string path;
        };

        sequence<string> Files;

        interface Session {

            // Create or close a seesion.
            int connect(string name);
            void close(int handle);

            // Send keepAlive message, and get events.
            ["amd"]Event keepAlive(int handle) throws UnknownSession, DuplicateKeepAlive;

            // Register or unregister events.
            void regEvent(int handle, Event e) throws UnknownSession;
            void unregEvent(int handle, Event e) throws UnknownSession;

            // Read or write file.
            string read(int handle, string path) throws UnknownSession, FileNotExist;
            void write(int handle, string path, string data) 
                throws UnknownSession, FileNotExist;

            // Directory operations.
            bool create(int handle, string path, bool temp) throws UnknownSession;
            void remove(int handle, string path) throws UnknownSession;
            bool exists(int handle, string path) throws UnknownSession;
            bool istmp(int handle, string path) throws UnknownSession;
            Files list(int handle, string path) throws UnknownSession;
        };
    };
};






