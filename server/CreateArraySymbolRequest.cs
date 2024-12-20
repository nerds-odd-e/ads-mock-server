using System.Text.Json;

namespace server
{
    public class CreateArraySymbolRequest
    {
        public string name {get; set;}
        public string type {get; set;}
        public int size {get; set;}
        public JsonElement value {get; set;}
    }
}