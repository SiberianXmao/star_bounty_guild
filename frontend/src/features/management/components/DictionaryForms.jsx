import { BadgePlus, Database, KeyRound, Plus, ShieldCheck, Undo2 } from "lucide-react";
import { getApiErrorMessage } from "../../../services/apiClient.js";
import {
  dictionaryDefaults,
  factionRelations,
  factionTypes,
  planetStatuses,
} from "../constants.js";
import { normalizeDictionaryPayload, updateDictionaryForm } from "../utils.js";
import { CheckboxField, InputField, ObjectSelect, SelectField, TextAreaField } from "./FormFields.jsx";
import PanelTitle from "./PanelTitle.jsx";
import styles from "../ManagementConsole.module.css";

export default function DictionaryForms({
  dictionaries,
  dictionaryForms,
  dictionaryMutation,
  setDictionaryForms,
}) {
  return (
    <section className={styles.dictionaryGrid}>
      <DictionaryCard
        icon={ShieldCheck}
        title="Фракция"
        type="faction"
        form={dictionaryForms.faction}
        mutation={dictionaryMutation}
        setForms={setDictionaryForms}
      >
        <InputField form={dictionaryForms.faction} label="Название" name="name" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "faction", updater)} />
        <TextAreaField form={dictionaryForms.faction} label="Описание" name="description" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "faction", updater)} />
        <SelectField form={dictionaryForms.faction} label="Тип" name="type" options={factionTypes} required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "faction", updater)} />
        <InputField form={dictionaryForms.faction} label="Влияние" max="100" min="0" name="influenceLevel" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "faction", updater)} type="number" />
        <SelectField form={dictionaryForms.faction} label="Отношение" name="relationToGuild" options={factionRelations} required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "faction", updater)} />
      </DictionaryCard>

      <DictionaryCard
        icon={Database}
        title="Сектор"
        type="sector"
        form={dictionaryForms.sector}
        mutation={dictionaryMutation}
        setForms={setDictionaryForms}
      >
        <InputField form={dictionaryForms.sector} label="Название" name="name" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "sector", updater)} />
        <TextAreaField form={dictionaryForms.sector} label="Описание" name="description" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "sector", updater)} />
        <ObjectSelect form={dictionaryForms.sector} label="Контроль" name="controllingFactionId" options={dictionaries.factions ?? []} setForm={(updater) => updateDictionaryForm(setDictionaryForms, "sector", updater)} />
        <InputField form={dictionaryForms.sector} label="Стабильность" max="100" min="0" name="stabilityLevel" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "sector", updater)} type="number" />
        <InputField form={dictionaryForms.sector} label="Опасность" max="100" min="0" name="dangerLevel" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "sector", updater)} type="number" />
      </DictionaryCard>

      <DictionaryCard
        icon={Database}
        title="Планета"
        type="planet"
        form={dictionaryForms.planet}
        mutation={dictionaryMutation}
        setForms={setDictionaryForms}
      >
        <InputField form={dictionaryForms.planet} label="Название" name="name" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} />
        <ObjectSelect form={dictionaryForms.planet} label="Сектор" name="sectorId" options={dictionaries.sectors ?? []} required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} />
        <ObjectSelect form={dictionaryForms.planet} label="Контроль" name="controllingFactionId" options={dictionaries.factions ?? []} setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} />
        <TextAreaField form={dictionaryForms.planet} label="Описание" name="description" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} />
        <InputField form={dictionaryForms.planet} label="Климат" name="climate" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} />
        <InputField form={dictionaryForms.planet} label="Население" min="0" name="population" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} type="number" />
        <InputField form={dictionaryForms.planet} label="Опасность" max="100" min="0" name="dangerLevel" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} type="number" />
        <InputField form={dictionaryForms.planet} label="Развитие" max="100" min="0" name="developmentLevel" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} type="number" />
        <SelectField form={dictionaryForms.planet} label="Статус" name="status" options={planetStatuses} required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "planet", updater)} />
      </DictionaryCard>

      <DictionaryCard
        icon={BadgePlus}
        title="Категория"
        type="category"
        form={dictionaryForms.category}
        mutation={dictionaryMutation}
        setForms={setDictionaryForms}
      >
        <InputField form={dictionaryForms.category} label="Название" name="name" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "category", updater)} />
        <InputField form={dictionaryForms.category} label="Slug" name="slug" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "category", updater)} />
        <TextAreaField form={dictionaryForms.category} label="Описание" name="description" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "category", updater)} />
        <CheckboxField form={dictionaryForms.category} label="Активна" name="active" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "category", updater)} />
      </DictionaryCard>

      <DictionaryCard
        icon={KeyRound}
        title="Валюта"
        type="currency"
        form={dictionaryForms.currency}
        mutation={dictionaryMutation}
        setForms={setDictionaryForms}
      >
        <InputField form={dictionaryForms.currency} label="Код" name="code" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "currency", updater)} />
        <InputField form={dictionaryForms.currency} label="Название" name="name" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "currency", updater)} />
        <InputField form={dictionaryForms.currency} label="Символ" name="symbol" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "currency", updater)} />
        <CheckboxField form={dictionaryForms.currency} label="Активна" name="active" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "currency", updater)} />
      </DictionaryCard>

      <DictionaryCard
        icon={Plus}
        title="Навык"
        type="skill"
        form={dictionaryForms.skill}
        mutation={dictionaryMutation}
        setForms={setDictionaryForms}
      >
        <InputField form={dictionaryForms.skill} label="Название" name="name" required setForm={(updater) => updateDictionaryForm(setDictionaryForms, "skill", updater)} />
        <TextAreaField form={dictionaryForms.skill} label="Описание" name="description" setForm={(updater) => updateDictionaryForm(setDictionaryForms, "skill", updater)} />
      </DictionaryCard>
    </section>
  );
}

function DictionaryCard({ children, form, icon, mutation, setForms, title, type }) {
  const Icon = icon;
  const isCurrentMutation = mutation.variables?.type === type;

  return (
    <section className={styles.dictionaryPanel}>
      <PanelTitle icon={Icon} title={title} />
      <form
        className={styles.form}
        onSubmit={(event) => {
          event.preventDefault();
          mutation.mutate({ payload: normalizeDictionaryPayload(type, form), type });
        }}
      >
        {children}
        {mutation.isError && isCurrentMutation ? (
          <div className="notice error">{getApiErrorMessage(mutation.error)}</div>
        ) : null}
        <div className={styles.formActions}>
          <button className="button" disabled={mutation.isPending && isCurrentMutation} type="submit">
            <Plus size={17} aria-hidden="true" />
            Добавить
          </button>
          <button
            className="button ghost"
            type="button"
            onClick={() =>
              setForms((current) => ({
                ...current,
                [type]: dictionaryDefaults[type],
              }))
            }
          >
            <Undo2 size={17} aria-hidden="true" />
            Сброс
          </button>
        </div>
      </form>
    </section>
  );
}
