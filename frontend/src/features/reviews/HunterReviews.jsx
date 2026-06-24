import { MessageSquareText, Star } from "lucide-react";
import { formatDate } from "../../utils/formatters.js";
import styles from "./HunterReviews.module.css";

export default function HunterReviews({ isLoading, reviews }) {
  if (isLoading) {
    return <p className={styles.empty}>Загрузка отзывов...</p>;
  }

  if (!reviews.length) {
    return (
      <div className={styles.emptyState}>
        <MessageSquareText size={21} aria-hidden="true" />
        <div>
          <strong>Письменных отзывов пока нет</strong>
          <span>Первый появится после оценки завершенного контракта.</span>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.list}>
      {reviews.map((review) => (
        <article className={styles.review} key={review.id}>
          <header>
            <div>
              <strong>{review.clientName || "Заказчик гильдии"}</strong>
              <span>{review.orderTitle || "Завершенный контракт"}</span>
            </div>
            <div className={styles.score} aria-label={`${review.rating} из 5`}>
              <Star size={15} fill="currentColor" aria-hidden="true" />
              <b>{review.rating}.0</b>
            </div>
          </header>
          {review.comment ? <p>{review.comment}</p> : <p className={styles.noComment}>Без комментария</p>}
          <time dateTime={review.createdAt}>{formatDate(review.createdAt)}</time>
        </article>
      ))}
    </div>
  );
}
