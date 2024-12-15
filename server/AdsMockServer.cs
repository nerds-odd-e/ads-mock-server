using System;
using System.Collections.Generic;
using System.Net;
using TwinCAT.Ads;
using TwinCAT.Ads.Server;
using TwinCAT.Ads.Server.TypeSystem;
using TwinCAT.Ads.TypeSystem;
using TwinCAT.Ams;
using TwinCAT.TypeSystem;

namespace server
{
    public class AdsMockServer : AdsSymbolicServer
    {

        static ushort s_Port = 851;

        SymbolicAnyTypeMarshaler _symbolMarshaler = new();

        private DataArea dataArea = new("dataArea", 0x01, 0x1000, 0x1000);
        private PrimitiveType dtInt = new("INT", typeof(short));

        private Dictionary<string, object> symbolValues = new();

        public AdsMockServer()
            : base(s_Port, "Ads Mock Server", null)
        {

            IPAddress ipEndpoint;
            if( !IPAddress.TryParse(System.Environment.GetEnvironmentVariable("ENV_AmsConfiguration__LoopbackAddress"), out ipEndpoint))
            {
                ipEndpoint = IPAddress.Loopback;
            }

            int port;
            if( ! int.TryParse(System.Environment.GetEnvironmentVariable("ENV_AmsConfiguration__LoopbackPort"), out port))
            {
                port = 48898;
            }

            AmsConfiguration.RouterEndPoint = new IPEndPoint( ipEndpoint, port);
        }

        protected override void OnConnected()
        {
            Init();
            base.OnConnected();
        }

        private void Init()
        {
            symbolFactory.AddType(dtInt);

            symbolFactory.AddDataArea(dataArea);
        }

        protected override AdsErrorCode OnReadRawValue(ISymbol symbol, Span<byte> span)
        {
            object value;
            AdsErrorCode ret = OnGetValue(symbol, out value);

            if (value != null)
            {
                int bytes = 0;
                if (_symbolMarshaler.TryMarshal(symbol, value, span, out bytes))
                {
                    ret = AdsErrorCode.NoError;
                }
                else
                {
                    ret = AdsErrorCode.DeviceInvalidSize;
                }

            }
            return ret;
        }

        protected override AdsErrorCode OnWriteRawValue(ISymbol symbol, ReadOnlySpan<byte> span)
        {
            throw new NotImplementedException();
        }

        protected override AdsErrorCode OnSetValue(ISymbol symbol, object value, out bool valueChanged)
        {
            throw new NotImplementedException();
        }

        protected override AdsErrorCode OnGetValue(ISymbol symbol, out object value)
        {
            value = symbolValues[symbol.InstancePath];
            return AdsErrorCode.NoError;
        }

        public void AddSymbol(CreateSymbolRequest request)
        {
            symbolFactory.AddSymbol(request.name, dtInt, dataArea);
            symbolValues[request.name] = (short)request.value;
        }
    }
}