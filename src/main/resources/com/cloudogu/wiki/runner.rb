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

locale = wikiContextFactory.get().getRequest().getLocale()
if locale.to_s == "de"
    dir = File.dirname(File.expand_path(__FILE__))
# TODO: Set correct path for template resources
    Precious::App.set(:mustache, {:templates => "#{dir}/src/main/resources/templates/"})
    puts "Setting german templates"
else
    puts "Do not set german templates"
end

Gollum::Hook.register(:post_commit, :hook_id) do |committer, sha1|
  provider = wikiContextFactory.get().getProvider()
  provider.push(wiki.getName(), sha1)
end

MapGollum.new(Precious::App, wiki)
