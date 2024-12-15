using System;
using Microsoft.AspNetCore.Mvc;

namespace server.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class AdsController : ControllerBase
    {
        private readonly AdsMockServer _adsMockServer;

        public AdsController(AdsMockServer adsMockServer)
        {
            _adsMockServer = adsMockServer;
        }

        [HttpPost ("/symbols")]
        public void CreateSymbol([FromBody] CreateSymbolRequest request)
        {
            _adsMockServer.AddSymbol(request);
        }
    }
}