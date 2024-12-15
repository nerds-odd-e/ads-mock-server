using System;
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
            AddSymbols();
            base.OnConnected();
        }

        private void AddSymbols()
        {
            PrimitiveType dtInt = new PrimitiveType("INT", typeof(short)); // 2-Byte size

            symbolFactory.AddType(dtInt);

            DataArea pcPlc = new DataArea("PC_PLC", 0x01, 0x1000, 0x1000);

            symbolFactory.AddDataArea(pcPlc);

            symbolFactory.AddSymbol("PC_PLC.b_error", dtInt, pcPlc);
        }

        protected override AdsErrorCode OnReadRawValue(ISymbol symbol, Span<byte> span)
        {
            short value = 42;
            // AdsErrorCode ret = OnGetValue(symbol, out value);
            AdsErrorCode ret = AdsErrorCode.NoError;

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
            throw new NotImplementedException();
        }
    }
}