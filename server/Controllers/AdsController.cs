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
            Program.server.AddSymbol(request);
        }
    }
}