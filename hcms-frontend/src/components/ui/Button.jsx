export default function Button({ children, variant = 'primary', ...props }) {
  const base = 'px-4 py-2 rounded font-medium transition'
  const variants = { primary: 'bg-primary-600 text-white hover:bg-primary-700', secondary: 'bg-gray-200 hover:bg-gray-300', danger: 'bg-red-600 text-white hover:bg-red-700' }
  return <button className={`${base} ${variants[variant]}`} {...props}>{children}</button>
}



