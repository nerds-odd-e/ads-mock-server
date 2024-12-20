using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using server.Controllers;
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

        SymbolicAnyTypeMarshaler _symbolMarshaler = new(Encoding.UTF8);

        private DataArea dataArea = new("dataArea", 0x01, 0x1000, 0x1000);
        private PrimitiveType dtInt = new("INT", typeof(short));
        private PrimitiveType dtBool = new("BOOL", typeof(bool)); // 1-Byte size
        private PrimitiveType dtLReal = new("LREAL", typeof(double)); // 8-Byte floating point
        private PrimitiveType dtDInt = new("DINT", typeof(int)); // 4-Byte size
        private PrimitiveType dtReal = new("REAL", typeof(float)); // 4-Byte floating point

        private Dictionary<string, object> symbolValues = new();
        private SetDeviceInfoRequest deviceInfo = new();

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

        [Obsolete]
        protected override Task<AdsErrorCode> ReadDeviceInfoIndicationAsync(AmsAddress sender, uint invokeId, CancellationToken cancel)
        {
            AdsVersion adsVersion = new AdsVersion(deviceInfo.version, deviceInfo.revision, deviceInfo.build);
            return ReadDeviceInfoResponseAsync(sender, invokeId, AdsErrorCode.NoError, deviceInfo.name, adsVersion, cancel);
        }

        protected override AdsErrorCode OnWriteRawValue(ISymbol symbol, ReadOnlySpan<byte> span)
        {
            Console.WriteLine("OnWriteRawValue called for symbol: {0} and length: {1}", symbol.InstancePath, span.Length);
            return AdsErrorCode.NoError;
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
            switch (request.type)
            {
                case "INT":
                    symbolFactory.AddSymbol(request.name, dtInt, dataArea);
                    symbolValues[request.name] = request.value.GetInt16();
                    break;
                case "DINT":
                    symbolFactory.AddSymbol(request.name, dtDInt, dataArea);
                    symbolValues[request.name] = request.value.GetInt32();
                    break;
                case "BOOL":
                    symbolFactory.AddSymbol(request.name, dtBool, dataArea);
                    symbolValues[request.name] = request.value.GetBoolean();
                    break;
                case "LREAL":
                    symbolFactory.AddSymbol(request.name, dtLReal, dataArea);
                    symbolValues[request.name] = request.value.GetDouble();
                    break;
                case "REAL":
                    symbolFactory.AddSymbol(request.name, dtReal, dataArea);
                    symbolValues[request.name] = request.value.GetSingle();
                    break;
            }
        }

        public void Connect()
        {
            Console.WriteLine("Starting the AdsSymbolicServer ...\n");
            ConnectServer();
            Console.WriteLine($"Symbolic Test Server runnning on Address: '{ServerAddress}' ...\n");
            Console.WriteLine($"For testing the server see the ReadMe.md file in project root");
            Console.WriteLine($"or type the following command from Powrshell with installed 'TcXaeMgmt' module:\n");
            Console.WriteLine($"PS> test-adsroute -NetId {ServerAddress.NetId} -port {ServerAddress.Port}\n\n");
        }

        public void ClearAllSymbols()
        {
            symbolFactory.ClearSymbols();
            symbolValues.Clear();
            deviceInfo = new SetDeviceInfoRequest {name = "not_set", version= 0, revision= 0, build= 0};
        }

        public void AddArraySymbol(CreateArraySymbolRequest request)
        {
            switch (request.type)
            {
                case "LREAL":
                    ArrayType doubleArrayType = new ArrayType(dtLReal, new DimensionCollection().AddDimension(new Dimension(0, request.size)));
                    symbolFactory.AddSymbol(request.name, doubleArrayType, dataArea);
                    symbolValues[request.name] = request.value.EnumerateArray().Select(x => x.GetDouble()).ToArray();
                    break;
            }
        }

        public void SetDeviceInfo(SetDeviceInfoRequest request)
        {
            this.deviceInfo = request;
        }
    }
}