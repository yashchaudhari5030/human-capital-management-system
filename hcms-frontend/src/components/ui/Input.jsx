export default function Input({ label, error, ...props }) {
  return (
    <div className="flex flex-col gap-1">
      {label && <label className="text-sm font-medium">{label}</label>}
      <input className={`border rounded p-2 ${error ? 'border-red-500' : 'border-gray-300'}`} {...props} />
      {error && <span className="text-red-500 text-xs">{error}</span>}
    </div>
  )
}



