export function export2Excel(header, val, listData, title) {
  require.ensure([], () => {
    const { export_json_to_excel } = require('@/excel/Export2Excel');
      const tHeader = header;
      const filterVal = val;
      const list = listData;  //把data里的tableData存到list
      const data = formatJson(filterVal, list);
      export_json_to_excel(tHeader, data, title);
    })
}

function formatJson(filterVal, jsonData) {
  return jsonData.map(v => filterVal.map(j => v[j]))
}