import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Send, Star } from "lucide-react";
import { reviewsApi } from "../../services/bountyApi.js";
import { getApiErrorMessage } from "../../services/apiClient.js";
import styles from "./HunterReviewForm.module.css";

const ratingLabels = ["", "Неудовлетворительно", "Слабо", "Хорошо", "Отлично", "Безупречно"];

export default function HunterReviewForm({ onSubmitted, order }) {
  const queryClient = useQueryClient();
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState("");

  const mutation = useMutation({
    mutationFn: () => reviewsApi.create(order.id, {
      rating,
      comment: comment.trim() || null,
    }),
    onSuccess: (review) => {
      queryClient.invalidateQueries({ queryKey: ["reviews"] });
      queryClient.invalidateQueries({ queryKey: ["hunter", review.hunterProfileId] });
      queryClient.invalidateQueries({ queryKey: ["hunters"] });
      queryClient.invalidateQueries({ queryKey: ["profiles"] });
      onSubmitted?.(review);
    },
  });

  return (
    <form
      className={styles.form}
      onSubmit={(event) => {
        event.preventDefault();
        if (rating > 0) mutation.mutate();
      }}
    >
      <div className={styles.heading}>
        <div>
          <span>Оценка выполненного контракта</span>
          <strong>{order.assignedHunterCallsign || "Охотник гильдии"}</strong>
        </div>
        <span className={styles.ratingLabel}>{rating ? ratingLabels[rating] : "Выберите оценку"}</span>
      </div>

      <div className={styles.stars} aria-label="Оценка охотника">
        {[1, 2, 3, 4, 5].map((value) => (
          <button
            aria-label={`${value} из 5`}
            data-active={value <= rating || undefined}
            key={value}
            title={`${value} из 5: ${ratingLabels[value]}`}
            type="button"
            onClick={() => setRating(value)}
          >
            <Star size={25} fill={value <= rating ? "currentColor" : "none"} aria-hidden="true" />
          </button>
        ))}
      </div>

      <label className={styles.comment}>
        <span>Комментарий <small>необязательно</small></span>
        <textarea
          maxLength={2000}
          placeholder="Что охотник сделал особенно хорошо?"
          value={comment}
          onChange={(event) => setComment(event.target.value)}
        />
      </label>

      {mutation.isError ? (
        <div className="notice error">{getApiErrorMessage(mutation.error)}</div>
      ) : null}

      <button className="button" disabled={!rating || mutation.isPending} type="submit">
        <Send size={16} aria-hidden="true" />
        {mutation.isPending ? "Отправляем..." : "Опубликовать отзыв"}
      </button>
    </form>
  );
}
