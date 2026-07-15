package com.stud.dictionary.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class V104__import_catalog_articles extends BaseJavaMigration {

    private static final Pattern HEADING = Pattern.compile("^##\\s+(.+?)\\R", Pattern.MULTILINE);

    @Override
    public void migrate(Context context) throws Exception {
        importArticles(context, "db/catalog/articles/planets.md", "bounty.planets", 16);
        importArticles(context, "db/catalog/articles/sectors.md", "bounty.sectors", 10);
        importArticles(context, "db/catalog/articles/factions.md", "bounty.factions", 10);
    }

    private void importArticles(
            Context context,
            String resourceName,
            String tableName,
            int expectedArticles
    ) throws IOException, SQLException {
        List<Article> articles = parseArticles(readResource(resourceName));
        if (articles.size() != expectedArticles) {
            throw new IllegalStateException(
                    "Expected %d articles in %s, found %d"
                            .formatted(expectedArticles, resourceName, articles.size())
            );
        }

        String sql = "UPDATE " + tableName + " SET description = ?, updated_at = now() WHERE name = ?";
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Article article : articles) {
                statement.setString(1, article.body());
                statement.setString(2, article.name());
                int updatedRows = statement.executeUpdate();

                if (updatedRows != 1) {
                    throw new IllegalStateException(
                            "Catalog article '%s' did not match exactly one row in %s"
                                    .formatted(article.name(), tableName)
                    );
                }
            }
        }
    }

    private List<Article> parseArticles(String markdown) {
        Matcher matcher = HEADING.matcher(markdown);
        List<SectionStart> starts = new ArrayList<>();

        while (matcher.find()) {
            starts.add(new SectionStart(matcher.group(1).trim(), matcher.end()));
        }

        List<Article> articles = new ArrayList<>();
        for (int index = 0; index < starts.size(); index++) {
            SectionStart start = starts.get(index);
            int end = index + 1 < starts.size()
                    ? markdown.lastIndexOf("##", starts.get(index + 1).contentStart())
                    : markdown.length();
            articles.add(new Article(start.name(), markdown.substring(start.contentStart(), end).trim()));
        }

        return articles;
    }

    private String readResource(String resourceName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IllegalStateException("Catalog resource not found: " + resourceName);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private record SectionStart(String name, int contentStart) {
    }

    private record Article(String name, String body) {
    }
}
