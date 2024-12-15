using System;
using Microsoft.AspNetCore.Mvc;

namespace server.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class AdsController : ControllerBase
    {
        [HttpPost ("/symbols")]
        public void CreateSymbol([FromBody] CreateSymbolRequest request)
        {
            Console.WriteLine($"Got a call to create symbol {request.name} of type {request.type} with value: {request.value}");
        }

        public class CreateSymbolRequest
        {
            public string name {get; set;}
            public string type {get; set;}
            public int value {get; set;}
        }
    }
}