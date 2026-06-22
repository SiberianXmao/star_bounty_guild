import { useEffect, useRef, useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { ImagePlus, LoaderCircle, Trash2, Upload } from "lucide-react";
import { useAuth } from "../../context/AuthContext.jsx";
import { authApi } from "../../services/bountyApi.js";
import { getApiErrorMessage } from "../../services/apiClient.js";
import styles from "./AvatarUploader.module.css";

const MAX_FILE_SIZE = 5 * 1024 * 1024;
const SUPPORTED_TYPES = new Set(["image/jpeg", "image/png"]);

export default function AvatarUploader({ avatarUrl, displayName }) {
  const inputRef = useRef(null);
  const { refreshUser } = useAuth();
  const [isDragging, setIsDragging] = useState(false);
  const [previewUrl, setPreviewUrl] = useState("");
  const [validationError, setValidationError] = useState("");

  const uploadMutation = useMutation({
    mutationFn: authApi.uploadAvatar,
    onSuccess: async () => {
      await refreshUser();
      setPreviewUrl("");
    },
    onError: () => setPreviewUrl(""),
  });

  const deleteMutation = useMutation({
    mutationFn: authApi.deleteAvatar,
    onSuccess: refreshUser,
  });

  useEffect(
    () => () => {
      if (previewUrl) {
        URL.revokeObjectURL(previewUrl);
      }
    },
    [previewUrl],
  );

  const isPending = uploadMutation.isPending || deleteMutation.isPending;
  const initials =
    displayName
      ?.split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0])
      .join("")
      .toUpperCase() || "BG";

  const submitFile = (file) => {
    setValidationError("");
    uploadMutation.reset();
    deleteMutation.reset();

    if (!file) {
      return;
    }

    if (!SUPPORTED_TYPES.has(file.type)) {
      setValidationError("Поддерживаются только JPEG и PNG.");
      return;
    }

    if (file.size > MAX_FILE_SIZE) {
      setValidationError("Размер изображения не должен превышать 5 МБ.");
      return;
    }

    setPreviewUrl(URL.createObjectURL(file));
    uploadMutation.mutate(file);
  };

  const handleDrop = (event) => {
    event.preventDefault();
    setIsDragging(false);
    submitFile(event.dataTransfer.files?.[0]);
  };

  const error =
    validationError ||
    (uploadMutation.isError ? getApiErrorMessage(uploadMutation.error) : "") ||
    (deleteMutation.isError ? getApiErrorMessage(deleteMutation.error) : "");

  return (
    <div className={styles.uploader}>
      <div
        className={`${styles.dropZone} ${isDragging ? styles.dragging : ""}`}
        role="button"
        tabIndex={0}
        aria-label="Загрузить аватар"
        aria-busy={isPending}
        title="Перетащите изображение или выберите файл"
        onClick={() => inputRef.current?.click()}
        onDragEnter={(event) => {
          event.preventDefault();
          setIsDragging(true);
        }}
        onDragOver={(event) => event.preventDefault()}
        onDragLeave={() => setIsDragging(false)}
        onDrop={handleDrop}
        onKeyDown={(event) => {
          if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            inputRef.current?.click();
          }
        }}
      >
        {previewUrl || avatarUrl ? (
          <img src={previewUrl || avatarUrl} alt="" />
        ) : (
          <strong>{initials}</strong>
        )}
        <span className={styles.uploadMark}>
          {isPending ? (
            <LoaderCircle className={styles.spinner} size={19} aria-hidden="true" />
          ) : (
            <Upload size={19} aria-hidden="true" />
          )}
        </span>
      </div>

      <input
        ref={inputRef}
        className={styles.fileInput}
        type="file"
        accept="image/jpeg,image/png"
        onChange={(event) => {
          submitFile(event.target.files?.[0]);
          event.target.value = "";
        }}
      />

      <div className={styles.actions}>
        <button type="button" onClick={() => inputRef.current?.click()} disabled={isPending}>
          <ImagePlus size={16} aria-hidden="true" />
          Изменить
        </button>
        {avatarUrl ? (
          <button
            className={styles.deleteButton}
            type="button"
            title="Удалить аватар"
            aria-label="Удалить аватар"
            disabled={isPending}
            onClick={() => deleteMutation.mutate()}
          >
            <Trash2 size={16} aria-hidden="true" />
          </button>
        ) : null}
      </div>

      <span className={styles.constraints}>JPG / PNG · до 5 МБ</span>
      {error ? <span className={styles.error}>{error}</span> : null}
    </div>
  );
}
