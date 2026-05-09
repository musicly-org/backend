package ly.music.catalog

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.queryForList

class ForeignKeyIndexTest : BackendControllerE2eTestSupport() {
    @Test
    fun everyForeignKeyHasSupportingIndex() {
        val missingIndexes =
            jdbcTemplate.queryForList<String>(
                """
                with foreign_keys as (
                    select
                        con.conname as constraint_name,
                        tbl.relname as table_name,
                        array_agg(att.attname order by key_pos.ordinality) as column_names
                    from pg_constraint con
                    join pg_class tbl on tbl.oid = con.conrelid
                    join unnest(con.conkey) with ordinality as key_pos(attnum, ordinality) on true
                    join pg_attribute att on att.attrelid = con.conrelid and att.attnum = key_pos.attnum
                    where con.contype = 'f'
                    group by con.conname, tbl.relname
                ),
                indexes as (
                    select
                        tbl.relname as table_name,
                        idx.relname as index_name,
                        array_agg(att.attname order by key_pos.ordinality) as column_names
                    from pg_index ind
                    join pg_class tbl on tbl.oid = ind.indrelid
                    join pg_class idx on idx.oid = ind.indexrelid
                    join unnest(ind.indkey) with ordinality as key_pos(attnum, ordinality) on true
                    join pg_attribute att on att.attrelid = ind.indrelid and att.attnum = key_pos.attnum
                    where ind.indisvalid
                      and ind.indpred is null
                      and ind.indexprs is null
                    group by tbl.relname, idx.relname
                )
                select fk.constraint_name
                from foreign_keys fk
                where not exists (
                    select 1
                    from indexes idx
                    where idx.table_name = fk.table_name
                      and idx.column_names[1:cardinality(fk.column_names)] = fk.column_names
                )
                order by fk.constraint_name
                """.trimIndent(),
            )

        assertThat(missingIndexes).isEmpty()
    }
}
