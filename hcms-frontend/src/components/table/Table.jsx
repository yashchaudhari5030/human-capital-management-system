export default function Table({ columns, data, onSort, sortKey, sortDir }) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full border-collapse bg-white shadow rounded">
        <thead className="bg-gray-100">
          <tr>
            {columns.map(c => (
              <th key={c.key} className="p-3 text-left cursor-pointer" onClick={() => onSort?.(c.key)}>
                {c.label} {sortKey === c.key && (sortDir === 'asc' ? '▲' : '▼')}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, i) => (
            <tr key={i} className="border-t hover:bg-gray-50">
              {columns.map(c => <td key={c.key} className="p-3">{c.render ? c.render(row) : row[c.key]}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}



