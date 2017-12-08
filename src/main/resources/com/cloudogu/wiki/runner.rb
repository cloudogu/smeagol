require 'rubygems'
require 'rack'
require 'gollum/app'
require 'rack/request'
require 'rack/response'

# fix table rendering
# https://github.com/gollum/gollum-lib/issues/227
GitHub::Markup::Markdown::MARKDOWN_GEMS['kramdown'] = proc { |content|
  Kramdown::Document.new(content, :auto_ids => false).to_html
}

# remove non used markup formats
Gollum::Page.send :remove_const, :FORMAT_NAMES if defined? Gollum::Page::FORMAT_NAMES
Gollum::Markup.formats.delete(:org)
Gollum::Markup.formats.delete(:rest)
Gollum::Markup.formats.delete(:mediawiki)
Gollum::Markup.formats.delete(:pod)
Gollum::Markup.formats.delete(:textile)

module Precious
  class App < Sinatra::Base

    def session
        account = settings.wikiContextFactory.get().getAccount()
        { 'gollum.author' => { :name => account.getDisplayName(), :email => account.getMail() }}
    end

  end
end


class MapGollum
    def initialize(app, wiki)
        @mg = Rack::Builder.new do
            map "/#{wiki.name}" do
                run app
            end
        end
    end

    def call(env)
        @mg.call(env)
    end
end

wiki_options = { :gollum_path => wikiOptions.repository, :page_file_dir => wikiOptions.directory, :universal_toc => wikiOptions.universalToc, :repo_is_bare => wikiOptions.bareRepo, :live_preview => wikiOptions.livePreview }

Precious::App.set(:gollum_path, wikiOptions.repository)
Precious::App.set(:default_markup, :markdown)
Precious::App.set(:wiki_options, wiki_options)
Precious::App.set(:wikiContextFactory, wikiContextFactory)

locale = wikiContextFactory.get().getLocale().getLanguage()
if locale.to_s == "de"

    staticPath = ENV["SMEAGOL_STATIC_PATH"]
    if staticPath == nil
      dir = File.dirname(File.expand_path(__FILE__))
      staticPath = "#{dir}/src/main/webapp"
    end

    Precious::App.set(:mustache, {
        :templates => "#{staticPath}/WEB-INF/templates/de/",
        :views => "#{staticPath}/WEB-INF/views/de/"
    })
end

plantUmlUrl = ENV["PLANTUML_URL"]
if plantUmlUrl && plantUmlUrl.length > 0
  Gollum::Filter::PlantUML.configure do |config|
    config.url = plantUmlUrl
    # do not verify ssl, in order to work with self signed certificates
    config.verify_ssl = false
  end
end

Gollum::Hook.register(:post_commit, :hook_id) do |committer, sha1|
  if sha1.is_a?(String)
    provider = wikiContextFactory.get().getProvider()
    provider.push(wiki.getName(), sha1)
  end
end

MapGollum.new(Precious::App, wiki)
