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

        [HttpPost ("/array-symbols")]
        public void CreateArraySymbol([FromBody] CreateArraySymbolRequest request)
        {
            _adsMockServer.AddArraySymbol(request);
        }

        [HttpPut ("/device-info")]
        public void SetDeviceInfo([FromBody] SetDeviceInfoRequest request)
        {
            _adsMockServer.SetDeviceInfo(request);
        }

        [HttpDelete ("/symbols")]
        public void ClearAllSymbols()
        {
            _adsMockServer.ClearAllSymbols();
        }
    }

    public class SetDeviceInfoRequest
    {
        public string name { get; set; }
        public int version { get; set; }
        public int revision { get; set; }
        public int build { get; set; }
    }
}