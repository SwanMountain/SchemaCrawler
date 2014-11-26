/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

/**
 * Database and connection information. Created from metadata returned
 * by a JDBC call, and other sources of information.
 *
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableCatalog
  extends AbstractNamedObjectWithAttributes
  implements Catalog, Reducible
{
  private static final long serialVersionUID = 4051323422934251828L;

  private final MutableDatabaseInfo databaseInfo;
  private final MutableJdbcDriverInfo jdbcDriverInfo;
  private final MutableSchemaCrawlerInfo schemaCrawlerInfo;
  private final Set<Schema> schemas;
  private final ColumnDataTypes columnDataTypes = new ColumnDataTypes();
  private final NamedObjectList<MutableTable> tables = new NamedObjectList<>();
  private final NamedObjectList<MutableRoutine> routines = new NamedObjectList<>();
  private final NamedObjectList<MutableSynonym> synonyms = new NamedObjectList<>();
  private final NamedObjectList<MutableSequence> sequences = new NamedObjectList<>();

  MutableCatalog(final String name)
  {
    super(name);
    databaseInfo = new MutableDatabaseInfo();
    jdbcDriverInfo = new MutableJdbcDriverInfo();
    schemaCrawlerInfo = new MutableSchemaCrawlerInfo();
    schemas = new HashSet<>();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Schema#getColumnDataType(java.lang.String)
   */
  @Override
  public MutableColumnDataType getColumnDataType(final Schema schema,
                                                 final String name)
  {
    return columnDataTypes.lookup(schema, name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSystemColumnDataTypes()
   */
  @Override
  public Collection<ColumnDataType> getColumnDataTypes()
  {
    return new ArrayList<ColumnDataType>(columnDataTypes.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSystemColumnDataTypes()
   */
  @Override
  public Collection<ColumnDataType> getColumnDataTypes(final Schema schema)
  {
    final Collection<ColumnDataType> values = getColumnDataTypes();
    for (final Iterator<ColumnDataType> iterator = values.iterator(); iterator
      .hasNext();)
    {
      final ColumnDataType mutableColumnDataType = iterator.next();
      if (!mutableColumnDataType.getSchema().equals(schema))
      {
        iterator.remove();
      }
    }
    return values;
  }

  @Override
  public MutableDatabaseInfo getDatabaseInfo()
  {
    return databaseInfo;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getJdbcDriverInfo()
   */
  @Override
  public MutableJdbcDriverInfo getJdbcDriverInfo()
  {
    return jdbcDriverInfo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Routine getRoutine(final Schema schema, final String name)
  {
    return routines.lookup(schema, name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Schema#getRoutines()
   */
  @Override
  public Collection<Routine> getRoutines()
  {
    final List<MutableRoutine> values = routines.values();
    return new ArrayList<Routine>(values);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Schema#getRoutines()
   */
  @Override
  public Collection<Routine> getRoutines(final Schema schema)
  {
    final List<Routine> values = new ArrayList<Routine>(routines.values());
    for (final Iterator<Routine> iterator = values.iterator(); iterator
      .hasNext();)
    {
      final Routine routine = iterator.next();
      if (!routine.getSchema().equals(schema))
      {
        iterator.remove();
      }
    }
    return values;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSchema(java.lang.String)
   */
  @Override
  public Schema getSchema(final String name)
  {
    final Collection<Schema> schemas = getSchemaNames();
    for (final Schema schema: schemas)
    {
      if (schema.getFullName().equals(name))
      {
        return schema;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSchemaCrawlerInfo()
   */
  @Override
  public MutableSchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return schemaCrawlerInfo;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSchemas()
   */
  @Override
  public Collection<Schema> getSchemas()
  {
    final List<Schema> schemas = new ArrayList<Schema>(this.schemas);
    Collections.sort(schemas);
    return schemas;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSequence(schemacrawler.schema.Schema,
   *      java.lang.String)
   */
  @Override
  public MutableSequence getSequence(final Schema schemaRef, final String name)
  {
    return sequences.lookup(schemaRef, name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Schema#getSequences()
   */
  @Override
  public Collection<Sequence> getSequences()
  {
    return new ArrayList<Sequence>(sequences.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSequences(schemacrawler.schema.Schema)
   */
  @Override
  public Collection<Sequence> getSequences(final Schema schemaRef)
  {
    final Collection<Sequence> values = getSequences();
    for (final Iterator<Sequence> iterator = values.iterator(); iterator
      .hasNext();)
    {
      final Sequence mutableSequence = iterator.next();
      if (!mutableSequence.getSchema().equals(schemaRef))
      {
        iterator.remove();
      }
    }
    return values;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSynonym(schemacrawler.schema.Schema,
   *      java.lang.String)
   */
  @Override
  public MutableSynonym getSynonym(final Schema schemaRef, final String name)
  {
    return synonyms.lookup(schemaRef, name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSynonyms()
   */
  @Override
  public Collection<Synonym> getSynonyms()
  {
    return new ArrayList<Synonym>(synonyms.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Schema#getRoutines()
   */
  @Override
  public Collection<Synonym> getSynonyms(final Schema schemaRef)
  {
    final Collection<Synonym> values = getSynonyms();
    for (final Iterator<Synonym> iterator = values.iterator(); iterator
      .hasNext();)
    {
      final Synonym mutableSynonym = iterator.next();
      if (!mutableSynonym.getSchema().equals(schemaRef))
      {
        iterator.remove();
      }
    }
    return values;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSystemColumnDataType(java.lang.String)
   */
  @Override
  public MutableColumnDataType getSystemColumnDataType(final String name)
  {
    return getColumnDataType(new SchemaReference(), name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Catalog#getSystemColumnDataTypes()
   */
  @Override
  public Collection<ColumnDataType> getSystemColumnDataTypes()
  {
    return getColumnDataTypes(new SchemaReference());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Schema#getTable(java.lang.String)
   */
  @Override
  public MutableTable getTable(final Schema schemaRef, final String name)
  {
    return tables.lookup(schemaRef, name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Schema#getTables()
   */
  @Override
  public Collection<Table> getTables()
  {
    final List<Table> values = new ArrayList<Table>(tables.values());
    return values;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Schema#getTables()
   */
  @Override
  public Collection<Table> getTables(final Schema schema)
  {
    final Collection<Table> values = getTables();
    for (final Iterator<Table> iterator = values.iterator(); iterator.hasNext();)
    {
      final Table mutableTable = iterator.next();
      if (!mutableTable.getSchema().equals(schema))
      {
        iterator.remove();
      }
    }
    return values;
  }

  @Override
  public void reduce(final SchemaCrawlerOptions options)
  {
    if (options == null)
    {
      return;
    }

    final SchemasReducer schemasReducer = new SchemasReducer(options);
    schemasReducer.reduce(schemas);

    // Filter the list of tables based on grep criteria, and
    // parent-child relationships
    final TablesReducer tablesReducer = new TablesReducer(options);
    tablesReducer.reduce(tables);

    // Filter the list of routines based on grep criteria
    final RoutinesReducer routinesReducer = new RoutinesReducer(options);
    routinesReducer.reduce(routines);

    final SynonymsReducer synonymsReducer = new SynonymsReducer(options);
    synonymsReducer.reduce(synonyms);

    final SequencesReducer sequencesReducer = new SequencesReducer(options);
    sequencesReducer.reduce(sequences);
  }

  void addColumnDataType(final MutableColumnDataType columnDataType)
  {
    if (columnDataType != null)
    {
      columnDataTypes.add(columnDataType);
    }
  }

  void addRoutine(final MutableRoutine routine)
  {
    routines.add(routine);
  }

  Schema addSchema(final Schema schema)
  {
    schemas.add(schema);
    return schema;
  }

  Schema addSchema(final String catalogName, final String schemaName)
  {
    return addSchema(new SchemaReference(catalogName, schemaName));
  }

  void addSequence(final MutableSequence sequence)
  {
    sequences.add(sequence);
  }

  void addSynonym(final MutableSynonym synonym)
  {
    synonyms.add(synonym);
  }

  void addTable(final MutableTable table)
  {
    tables.add(table);
  }

  NamedObjectList<MutableRoutine> getAllRoutines()
  {
    return routines;
  }

  NamedObjectList<MutableSequence> getAllSequences()
  {
    return sequences;
  }

  NamedObjectList<MutableSynonym> getAllSynonyms()
  {
    return synonyms;
  }

  NamedObjectList<MutableTable> getAllTables()
  {
    return tables;
  }

  Collection<Schema> getSchemaNames()
  {
    return schemas;
  }

  MutableColumnDataType lookupColumnDataTypeByType(final int type)
  {
    return columnDataTypes.lookupColumnDataTypeByType(type);
  }

}