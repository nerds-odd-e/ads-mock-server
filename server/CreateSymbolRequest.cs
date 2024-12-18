using System.Text.Json;

namespace server
{
    public class CreateSymbolRequest
    {
        public string name {get; set;}
        public string type {get; set;}
        public JsonElement value {get; set;}
    }
}