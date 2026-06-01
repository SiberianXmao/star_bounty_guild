import { UserPlus } from "lucide-react";
import { getApiErrorMessage } from "../../../services/apiClient.js";
import { roleLabel } from "../labels.js";
import { InputField } from "./FormFields.jsx";
import styles from "../ManagementConsole.module.css";

export default function CreateUserForm({ form, mutation, roles, setForm }) {
  return (
    <form
      className={styles.form}
      onSubmit={(event) => {
        event.preventDefault();
        mutation.mutate();
      }}
    >
      <InputField form={form} label="Email" name="email" required setForm={setForm} type="email" />
      <InputField form={form} label="Username" minLength="3" name="username" required setForm={setForm} />
      <InputField form={form} label="Имя" name="displayName" setForm={setForm} />
      <InputField form={form} label="Пароль" minLength="8" name="password" required setForm={setForm} type="password" />
      <label className="field">
        <span>Первая роль</span>
        <select
          className="select"
          name="roleName"
          value={form.roleName}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              roleName: event.target.value,
            }))
          }
        >
          {roles.map((role) => (
            <option key={role} value={role}>
              {roleLabel(role)}
            </option>
          ))}
        </select>
      </label>
      {mutation.isError ? <div className="notice error">{getApiErrorMessage(mutation.error)}</div> : null}
      {mutation.isSuccess ? <div className="notice success">Пользователь создан</div> : null}
      <button className="button" disabled={mutation.isPending} type="submit">
        <UserPlus size={17} aria-hidden="true" />
        Создать
      </button>
    </form>
  );
}
