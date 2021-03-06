package org.apache.hadoop.hbase.themis.cp;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValue.Type;
import org.apache.hadoop.hbase.filter.Filter.ReturnCode;
import org.apache.hadoop.hbase.themis.TestBase;
import org.apache.hadoop.hbase.themis.cp.ColumnTimestampFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;

public class TestColumnTimestampFilter extends TestBase {
  @Test
  public void testFilterKeyValue() {
    ColumnTimestampFilter filter = new ColumnTimestampFilter();
    KeyValue kv = new KeyValue(ROW, FAMILY, QUALIFIER, PREWRITE_TS, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.NEXT_ROW, filter.filterKeyValue(kv));
    
    filter = new ColumnTimestampFilter();
    filter.addColumnTimestamp(COLUMN, PREWRITE_TS);
    kv = new KeyValue(ROW, ANOTHER_FAMILY, QUALIFIER, PREWRITE_TS, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.SEEK_NEXT_USING_HINT, filter.filterKeyValue(kv));
    Assert.assertArrayEquals(filter.getNextKeyHint(kv).getFamily(), FAMILY);
    Assert.assertArrayEquals(filter.getNextKeyHint(kv).getQualifier(), QUALIFIER);
    
    filter = new ColumnTimestampFilter();
    filter.addColumnTimestamp(COLUMN, PREWRITE_TS);
    kv = new KeyValue(ROW, FAMILY, ANOTHER_QUALIFIER, PREWRITE_TS, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.SEEK_NEXT_USING_HINT, filter.filterKeyValue(kv));
    Assert.assertArrayEquals(filter.getNextKeyHint(kv).getFamily(), FAMILY);
    Assert.assertArrayEquals(filter.getNextKeyHint(kv).getQualifier(), QUALIFIER);
    
    filter = new ColumnTimestampFilter();
    filter.addColumnTimestamp(COLUMN, PREWRITE_TS);
    byte[] laterFamily = Bytes.toBytes(Bytes.toString(FAMILY) + "#");
    kv = new KeyValue(ROW, laterFamily, QUALIFIER, PREWRITE_TS, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.NEXT_ROW, filter.filterKeyValue(kv));
    
    filter = new ColumnTimestampFilter();
    filter.addColumnTimestamp(COLUMN, PREWRITE_TS);
    kv = new KeyValue(ROW, FAMILY, QUALIFIER, PREWRITE_TS - 1, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.NEXT_COL, filter.filterKeyValue(kv));
    
    filter = new ColumnTimestampFilter();
    filter.addColumnTimestamp(COLUMN, PREWRITE_TS);
    kv = new KeyValue(ROW, FAMILY, QUALIFIER, PREWRITE_TS, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filter.filterKeyValue(kv));
    
    filter = new ColumnTimestampFilter();
    filter.addColumnTimestamp(COLUMN, PREWRITE_TS);
    kv = new KeyValue(ROW, FAMILY, QUALIFIER, PREWRITE_TS + 1, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.SKIP, filter.filterKeyValue(kv));
    
    filter = new ColumnTimestampFilter();
    filter.addColumnTimestamp(COLUMN, PREWRITE_TS);
    kv = new KeyValue(ROW, FAMILY, ANOTHER_QUALIFIER, PREWRITE_TS, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.SEEK_NEXT_USING_HINT, filter.filterKeyValue(kv));
    Assert.assertArrayEquals(filter.getNextKeyHint(kv).getFamily(), FAMILY);
    Assert.assertArrayEquals(filter.getNextKeyHint(kv).getQualifier(), QUALIFIER);
    kv = new KeyValue(ROW, FAMILY, QUALIFIER, PREWRITE_TS + 1, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.SKIP, filter.filterKeyValue(kv));
    kv = new KeyValue(ROW, FAMILY, QUALIFIER, PREWRITE_TS, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filter.filterKeyValue(kv));
    kv = new KeyValue(ROW, FAMILY, QUALIFIER, PREWRITE_TS - 1, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.NEXT_ROW, filter.filterKeyValue(kv));
    byte[] laterQualifier = Bytes.toBytes(Bytes.toString(QUALIFIER) + "#");
    kv = new KeyValue(ROW, FAMILY, laterQualifier, PREWRITE_TS + 1, Type.Put, VALUE);
    Assert.assertEquals(ReturnCode.NEXT_ROW, filter.filterKeyValue(kv));

  }
}
