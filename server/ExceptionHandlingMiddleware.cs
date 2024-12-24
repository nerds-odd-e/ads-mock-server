using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using static System.Text.Json.JsonSerializer;

namespace server
{
    public class ExceptionHandlingMiddleware
    {
        private readonly RequestDelegate _next;

        public ExceptionHandlingMiddleware(RequestDelegate next)
        {
            _next = next;
        }

        public async Task Invoke(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (BadHttpRequestException ex)
            {
                context.Response.StatusCode = StatusCodes.Status400BadRequest;
                context.Response.ContentType = "application/json";
                await context.Response.WriteAsync(Serialize(new
                {
                    error = ex.Message
                }));
            }
        }
    }
}