import { dictionaryLabel } from "../labels.js";
import { updateFormValue } from "../utils.js";
import styles from "../ManagementConsole.module.css";

export function InputField({ form, label, name, setForm, type = "text", ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input
        className="input"
        name={name}
        type={type}
        value={form[name]}
        onChange={(event) => updateFormValue(setForm, event)}
        {...props}
      />
    </label>
  );
}

export function TextAreaField({ form, label, name, setForm, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <textarea
        className="textarea"
        name={name}
        value={form[name]}
        onChange={(event) => updateFormValue(setForm, event)}
        {...props}
      />
    </label>
  );
}

export function SelectField({ form, label, name, options, required, setForm }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select
        className="select"
        name={name}
        required={required}
        value={form[name]}
        onChange={(event) => updateFormValue(setForm, event)}
      >
        {!required ? <option value="">Не указано</option> : null}
        {options.map((option) => (
          <option key={option} value={option}>
            {dictionaryLabel(option)}
          </option>
        ))}
      </select>
    </label>
  );
}

export function ObjectSelect({ form, label, name, options, required, setForm }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select
        className="select"
        name={name}
        required={required}
        value={form[name]}
        onChange={(event) => updateFormValue(setForm, event)}
      >
        <option value="">Не указано</option>
        {options.map((option) => (
          <option key={option.id ?? option.code} value={option.id ?? option.code}>
            {option.name ?? option.code}
          </option>
        ))}
      </select>
    </label>
  );
}

export function CheckboxField({ form, label, name, setForm }) {
  return (
    <label className={styles.checkbox}>
      <input
        checked={Boolean(form[name])}
        name={name}
        type="checkbox"
        onChange={(event) =>
          setForm((current) => ({
            ...current,
            [name]: event.target.checked,
          }))
        }
      />
      <span>{label}</span>
    </label>
  );
}
