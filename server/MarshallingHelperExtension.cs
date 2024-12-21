#nullable enable
using System.Runtime.InteropServices;

namespace TwinCAT.TypeSystem
{
  /// <summary>Class MarshallingHelperExtension.</summary>
  /// <exclude />
  internal static class MarshallingHelperExtension
  {
    /// <summary>Gets the marshal size of the symbol.</summary>
    /// <param name="symbol">The symbol.</param>
    /// <returns>System.Int32.</returns>
    internal static int GetValueMarshalSize(this ISymbol symbol)
    {
      return symbol.DataType == null ? (symbol.Category != DataTypeCategory.Reference ? symbol.ByteSize : ((IReferenceInstance) symbol).ResolvedByteSize) : symbol.DataType.GetValueMarshalSize();
    }

    /// <summary>
    /// Gets the marshal siize of the <see cref="T:TwinCAT.TypeSystem.IDataType" />
    /// </summary>
    /// <param name="type">The type.</param>
    /// <returns>System.Int32.</returns>
    internal static int GetValueMarshalSize(this IDataType type)
    {
      return type.Category != DataTypeCategory.Reference ? type.ByteSize : ((IReferenceType) type).ResolvedByteSize;
    }

  }
}
