import { useRef, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ImagePlus, LoaderCircle, Trash2, Upload } from "lucide-react";
import { dictionaryApi } from "../../services/bountyApi.js";
import { getApiErrorMessage } from "../../services/apiClient.js";
import styles from "./PlanetImageUploader.module.css";

const MAX_FILE_SIZE = 5 * 1024 * 1024;
const ACCEPTED_TYPES = new Set(["image/jpeg", "image/png"]);
const entityLabels = {
  factions: "фракции",
  planets: "планеты",
  sectors: "сектора",
};

export default function PlanetImageUploader({ entity, type }) {
  const queryClient = useQueryClient();
  const inputRef = useRef(null);
  const [dragging, setDragging] = useState(false);
  const [localError, setLocalError] = useState("");

  const refreshDictionaries = () => {
    queryClient.invalidateQueries({ queryKey: ["dictionaries"] });
  };

  const uploadMutation = useMutation({
    mutationFn: (file) => dictionaryApi.uploadCatalogImage(type, entity.id, file),
    onSuccess: refreshDictionaries,
  });

  const deleteMutation = useMutation({
    mutationFn: () => dictionaryApi.deleteCatalogImage(type, entity.id),
    onSuccess: refreshDictionaries,
  });

  const chooseFile = (file) => {
    setLocalError("");
    if (!file) return;

    if (!ACCEPTED_TYPES.has(file.type)) {
      setLocalError("Выберите JPEG или PNG");
      return;
    }

    if (file.size > MAX_FILE_SIZE) {
      setLocalError("Файл должен быть не больше 5 МБ");
      return;
    }

    uploadMutation.mutate(file);
  };

  const isPending = uploadMutation.isPending || deleteMutation.isPending;
  const mutationError = uploadMutation.error || deleteMutation.error;

  return (
    <div className={styles.root}>
      <button
        className={styles.dropzone}
        data-dragging={dragging || undefined}
        disabled={isPending}
        type="button"
        onClick={() => inputRef.current?.click()}
        onDragEnter={(event) => {
          event.preventDefault();
          setDragging(true);
        }}
        onDragOver={(event) => event.preventDefault()}
        onDragLeave={() => setDragging(false)}
        onDrop={(event) => {
          event.preventDefault();
          setDragging(false);
          chooseFile(event.dataTransfer.files?.[0]);
        }}
      >
        {entity.imageUrl ? (
          <img src={entity.imageUrl} alt={`Изображение ${entityLabels[type]} ${entity.name}`} />
        ) : (
          <span className={styles.placeholder}>
            <ImagePlus size={20} aria-hidden="true" />
          </span>
        )}
        <span className={styles.copy}>
          {isPending ? (
            <LoaderCircle className={styles.spinner} size={16} aria-hidden="true" />
          ) : (
            <Upload size={16} aria-hidden="true" />
          )}
          <span>
            <strong>{entity.imageUrl ? "Заменить изображение" : "Добавить изображение"}</strong>
            <small>Перетащите или выберите JPEG/PNG</small>
          </span>
        </span>
      </button>

      <input
        ref={inputRef}
        accept="image/jpeg,image/png"
        className={styles.fileInput}
        type="file"
        onChange={(event) => {
          chooseFile(event.target.files?.[0]);
          event.target.value = "";
        }}
      />

      {entity.imageUrl ? (
        <button
          aria-label={`Удалить изображение ${entityLabels[type]} ${entity.name}`}
          className={styles.deleteButton}
          disabled={isPending}
          title="Удалить изображение"
          type="button"
          onClick={() => deleteMutation.mutate()}
        >
          <Trash2 size={15} aria-hidden="true" />
        </button>
      ) : null}

      {localError || mutationError ? (
        <span className={styles.error} role="alert">
          {localError || getApiErrorMessage(mutationError)}
        </span>
      ) : null}
    </div>
  );
}
