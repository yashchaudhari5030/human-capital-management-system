export default function Select({ label, options, error, ...props }) {
  return (
    <div className="flex flex-col gap-1">
      {label && <label className="text-sm font-medium">{label}</label>}
      <select className={`border rounded p-2 ${error ? 'border-red-500' : 'border-gray-300'}`} {...props}>
        <option value="">Select...</option>
        {options.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </select>
      {error && <span className="text-red-500 text-xs">{error}</span>}
    </div>
  )
}



