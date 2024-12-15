using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using TwinCAT;
using TwinCAT.Ads;
using TwinCAT.Ads.TypeSystem;
using TwinCAT.TypeSystem;

namespace server
{
    public class Program
    {
        static AdsMockServer server = new();

        public static void Main(string[] args)
        {
            Console.WriteLine("Starting the AdsSymbolicServer ...\n");
            server.ConnectServer();
            Console.WriteLine($"Symbolic Test Server runnning on Address: '{server.ServerAddress}' ...\n");
            Console.WriteLine($"For testing the server see the ReadMe.md file in project root");
            Console.WriteLine($"or type the following command from Powrshell with installed 'TcXaeMgmt' module:\n");
            Console.WriteLine($"PS> test-adsroute -NetId {server.ServerAddress.NetId} -port {server.ServerAddress.Port}\n\n");
            Console.WriteLine("Press the ENTER key to cancel...\n");

            // AdsSession session = new AdsSession(AmsNetId.Local, server.ServerPort);
            // session.Connect();
            // SymbolLoaderFactory.Create(session.Connection, SymbolLoaderSettings.Default);

            CreateHostBuilder(args).Build().Run();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureWebHostDefaults(webBuilder =>
                {
                    webBuilder.UseStartup<Startup>();
                });
    }
}
